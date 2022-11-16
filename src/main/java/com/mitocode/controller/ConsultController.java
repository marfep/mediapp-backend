package com.mitocode.controller;

import com.mitocode.dto.ConsultDTO;
import com.mitocode.dto.ConsultListExamDTO;
import com.mitocode.dto.ConsultProcDTO;
import com.mitocode.dto.FilterConsultDTO;
import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.model.Consult;
import com.mitocode.model.Exam;
import com.mitocode.model.MediaFile;
import com.mitocode.model.Medic;
import com.mitocode.service.IMediaFileService;
import com.mitocode.service.impl.ConsultServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/consults")
public class ConsultController {

    @Autowired
    private ConsultServiceImpl service;// = new ConsultServiceImpl();

    @Autowired
    private IMediaFileService mfService;

    @Autowired
    private ModelMapper mapper;

    @GetMapping
    public ResponseEntity<List<ConsultDTO>> findAll() {
        List<ConsultDTO> list = service.findAll().stream().map(p -> mapper.map(p, ConsultDTO.class)).collect(Collectors.toList());

        return new ResponseEntity<>(list, OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultDTO> findById(@PathVariable("id") Integer id) {
        Consult obj = service.findById(id);
        if (obj == null) {
            throw new ModelNotFoundException("ID NOT FOUND: " + id);
        } else {
            return new ResponseEntity<>(mapper.map(obj, ConsultDTO.class), OK);
        }
    }

    /*@PostMapping
    public ResponseEntity<Consult> save(@RequestBody Consult consult){
        Consult obj = service.save(consult);
        return new ResponseEntity<>(obj, CREATED);
    }*/

    /*@PostMapping
    public ResponseEntity<Void> save(@RequestBody ConsultDTO dto){
        Consult obj = service.save(mapper.map(dto, Consult.class));
        //localhost:8080/consults/5
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdConsult()).toUri();
        return ResponseEntity.created(location).build();
    }*/

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody ConsultListExamDTO dto) {
        Consult cons = mapper.map(dto.getConsult(), Consult.class);
        List<Exam> exams = mapper.map(dto.getLstExam(), new TypeToken<List<Exam>>() {
        }.getType());

        Consult obj = service.saveTransactional(cons, exams);
        //localhost:8080/consults/5
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdConsult()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping
    public ResponseEntity<Consult> update(@RequestBody ConsultDTO dto) {
        Consult obj = service.update(mapper.map(dto, Consult.class));
        return new ResponseEntity<>(obj, OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        Consult obj = service.findById(id);
        if (obj == null) {
            throw new ModelNotFoundException("ID NOT FOUND: " + id);
        }
        service.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @GetMapping("/hateoas/{id}")
    public EntityModel<ConsultDTO> findByIdHateoas(@PathVariable("id") Integer id) {
        Consult obj = service.findById(id);

        if (obj == null) {
            throw new ModelNotFoundException("ID NOT FOUND: " + id);
        }

        EntityModel<ConsultDTO> resource = EntityModel.of(mapper.map(obj, ConsultDTO.class));
        WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
        WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findById(id));
        resource.add(link1.withRel("consult-info1"));
        resource.add(link2.withRel("consult-info2"));
        return resource;
    }

    @PostMapping("/search/others")
    public ResponseEntity<List<ConsultDTO>> searchByOthers(@RequestBody FilterConsultDTO filterDTO) {
        List<Consult> consults = service.search(filterDTO.getDni(), filterDTO.getFullname());
        List<ConsultDTO> consultsDTO = mapper.map(consults, new TypeToken<List<ConsultDTO>>() {
        }.getType());

        return new ResponseEntity<>(consultsDTO, HttpStatus.OK);
    }

    //localhost:8080/consults/search/date?date1=xxxxx&date2=yyyyyy
    @GetMapping("/search/date")
    public ResponseEntity<List<ConsultDTO>> searchByDates(@RequestParam(value = "date1") String date1, @RequestParam(value = "date2") String date2) {
        List<Consult> consults = service.searchByDates(LocalDateTime.parse(date1), LocalDateTime.parse(date2));
        List<ConsultDTO> consultsDTO = mapper.map(consults, new TypeToken<List<ConsultDTO>>() {
        }.getType());

        return new ResponseEntity<>(consultsDTO, HttpStatus.OK);
    }

    @GetMapping("/callProcedure")
    public ResponseEntity<List<ConsultProcDTO>> callProcOrFunction() {
        List<ConsultProcDTO> consults = service.callProcedureOrFunction();
        return new ResponseEntity<>(consults, HttpStatus.OK);
    }

    @GetMapping(value = "/generateReport", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE) //APPLICATION_PDF_VALUE
    public ResponseEntity<byte[]> generateReport() throws Exception {
        byte[] data = null;
        data = service.generateReport();
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping(value = "/saveFile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> saveFile(@RequestParam("file") MultipartFile file) throws Exception { //@RequestPart("medic") Medic medic
        MediaFile mf = new MediaFile();
        mf.setFiletype(file.getContentType());
        mf.setFilename(file.getOriginalFilename());
        mf.setValue(file.getBytes());

        mfService.save(mf);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/readFile/{idFile}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> readFile(@PathVariable("idFile") Integer idFile) throws IOException {

        byte[] arr = mfService.findById(idFile).getValue();

        return new ResponseEntity<>(arr, HttpStatus.OK);
    }

    /*@GetMapping(produces = "application/xml")
    public Consult save(){
        Consult p = new Consult();
        return service.save(p);
    }*/


}
