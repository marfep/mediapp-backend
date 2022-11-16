package com.mitocode.controller;

import com.mitocode.dto.MedicDTO;
import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.model.Medic;
import com.mitocode.service.impl.MedicServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/medics")
public class MedicController {

    @Autowired
    private MedicServiceImpl service;// = new MedicServiceImpl();

    @Autowired
    private ModelMapper mapper;

    //@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    //@PreAuthorize("@authServiceImpl.hasAccess('findAll')")
    @GetMapping
    public ResponseEntity<List<MedicDTO>> findAll(){
        List<MedicDTO> list = service.findAll().stream().map(p -> mapper.map(p, MedicDTO.class)).collect(Collectors.toList());

        return new ResponseEntity<>(list, OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicDTO> findById(@PathVariable("id") Integer id){
        Medic obj = service.findById(id);
        if(obj == null){
            throw new ModelNotFoundException("ID NOT FOUND: " + id);
        }else{
            return new ResponseEntity<>(mapper.map(obj, MedicDTO.class), OK);
        }
    }

    /*@PostMapping
    public ResponseEntity<Medic> save(@RequestBody Medic medic){
        Medic obj = service.save(medic);
        return new ResponseEntity<>(obj, CREATED);
    }*/

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody MedicDTO dto){
        Medic obj = service.save(mapper.map(dto, Medic.class));
        //localhost:8080/medics/5
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdMedic()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping
    public ResponseEntity<Medic> update(@RequestBody MedicDTO dto){
        Medic obj = service.update(mapper.map(dto, Medic.class));
        return new ResponseEntity<>(obj, OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id){
        Medic obj = service.findById(id);
        if(obj == null){
            throw new ModelNotFoundException("ID NOT FOUND: " + id);
        }
        service.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @GetMapping("/hateoas/{id}")
    public EntityModel<MedicDTO> findByIdHateoas(@PathVariable("id") Integer id) {
        Medic obj = service.findById(id);

        if(obj == null){
            throw new ModelNotFoundException("ID NOT FOUND: " + id);
        }

        EntityModel<MedicDTO> resource = EntityModel.of(mapper.map(obj, MedicDTO.class));
        WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
        WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findById(id));
        resource.add(link1.withRel("medic-info1"));
        resource.add(link2.withRel("medic-info2"));
        return resource;
    }

    /*@GetMapping(produces = "application/xml")
    public Medic save(){
        Medic p = new Medic();
        return service.save(p);
    }*/

}
