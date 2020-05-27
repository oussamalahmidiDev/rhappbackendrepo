package com.gi.rhapp.controllers.rh;


import com.gi.rhapp.models.Absence;
import com.gi.rhapp.models.AvantageNat;
import com.gi.rhapp.models.Retraite;
import com.gi.rhapp.models.Salarie;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.Download;
import com.gi.rhapp.services.MailService;
import com.gi.rhapp.services.Upload;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/rh/api/absences")
@CrossOrigin("*")
public class RhAbsencesController {

    @Autowired
    private SalarieRepository salarieRepository;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private RetraiteRepository retraiteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AvantageNatRepository avantageNatRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private Upload uploadService;

    @Autowired
    private Download downloadService;





    //    **************************************************************************************************************************************************
    //    *********************************************** API get all absences ******************************************************************

    @GetMapping() //works
    public List<Absence> getAbsences(){
            return absenceRepository.findAllByOrderByDateCreationDesc();
    }

    @GetMapping("/{id}/justificatif/{filename}")
    public ResponseEntity<Resource> getJustificatif(HttpServletRequest request,@PathVariable("id") Long id, @PathVariable("filename") String filename) {

        Absence absence = absenceRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource introuvable")
        );

        if (absence.getJustificatif() == null)
            throw new ResponseStatusException(HttpStatus.OK, "Cet absence ne possède pas d'un justificatifif.");
        else if (!filename.equals(absence.getJustificatif()))
            throw new ResponseStatusException(HttpStatus.OK, "Fichier introuvable.");


        Resource resource = downloadService.downloadJustificatif(absence.getJustificatif());
        // setting content-type header
        String contentType = null;
        try {
            // setting content-type header according to file type
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Type indéfini.");
        }
        // setting content-type header to generic octet-stream
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }

    @PostMapping("/create")
    public Absence createAbsence(
        @RequestParam("salarie_id") Long salarieId,
        @RequestParam("type") String type,
        @RequestParam("dateDebut") Date dateDebut,
        @RequestParam("dateFin") Date dateFin,

        // i used @RequestPart here instead of @RequestParm to mark this param as optional
        @RequestPart(name = "justificatif", required = false) MultipartFile justificatif
    ) {
        Salarie salarie = salarieRepository.findById(salarieId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Salarie introuvable")
        );

        Absence absence = Absence.builder()
            .dateDebut(dateDebut)
            .dateFin(dateFin)
            .type(type)
            .salarie(salarie)
            .build();

        if (justificatif != null) {
            String filename = uploadService.uploadJustificatif(justificatif);
            absence.setJustificatif(filename);
        }

        return absenceRepository.save(absence);

    }

}

