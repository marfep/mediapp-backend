package com.mitocode.controller;

import com.mitocode.dto.PatientDTO;
import com.mitocode.dto.SignDTO;
import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.model.Sign;
import com.mitocode.service.impl.SignServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/signs")
public class SignController {

    @Autowired
    private SignServiceImpl service;// = new SignServiceImpl();

    @Autowired
    private ModelMapper mapper;

    @GetMapping
    public ResponseEntity<List<SignDTO>> findAll(){
        List<SignDTO> list = service.findAll().stream().map(p -> mapper.map(p, SignDTO.class)).collect(Collectors.toList());

        return new ResponseEntity<>(list, OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SignDTO> findById(@PathVariable("id") Integer id){
        Sign obj = service.findById(id);
        if(obj == null){
            throw new ModelNotFoundException("ID NOT FOUND: " + id);
        }else{
            return new ResponseEntity<>(mapper.map(obj, SignDTO.class), OK);
        }
    }

    /*@PostMapping
    public ResponseEntity<Sign> save(@RequestBody Sign sign){
        Sign obj = service.save(sign);
        return new ResponseEntity<>(obj, CREATED);
    }*/

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody SignDTO dto){
        Sign obj = service.save(mapper.map(dto, Sign.class));
        //localhost:8080/signs/5
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdSign()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping
    public ResponseEntity<Sign> update(@RequestBody SignDTO dto){
        Sign obj = service.update(mapper.map(dto, Sign.class));
        return new ResponseEntity<>(obj, OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id){
        Sign obj = service.findById(id);
        if(obj == null){
            throw new ModelNotFoundException("ID NOT FOUND: " + id);
        }
        service.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @GetMapping("/hateoas/{id}")
    public EntityModel<SignDTO> findByIdHateoas(@PathVariable("id") Integer id) {
        Sign obj = service.findById(id);

        if(obj == null){
            throw new ModelNotFoundException("ID NOT FOUND: " + id);
        }

        EntityModel<SignDTO> resource = EntityModel.of(mapper.map(obj, SignDTO.class));
        WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
        WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findById(id));
        resource.add(link1.withRel("sign-info1"));
        resource.add(link2.withRel("sign-info2"));
        return resource;
    }

    @GetMapping("/pageable")
    public ResponseEntity<Page<SignDTO>> listPage(Pageable pageable) {
        Page<SignDTO> page = service.listPage(pageable).map(p -> mapper.map(p, SignDTO.class));

        return new ResponseEntity<>(page, HttpStatus.OK);

    }
    /*@GetMapping(produces = "application/xml")
    public Sign save(){
        Sign p = new Sign();
        return service.save(p);
    }*/

}
