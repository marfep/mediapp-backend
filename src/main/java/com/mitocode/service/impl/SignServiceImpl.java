package com.mitocode.service.impl;

import com.mitocode.model.Patient;
import com.mitocode.model.Sign;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.repo.ISignRepo;
import com.mitocode.service.ISignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SignServiceImpl extends CRUDImpl<Sign,Integer> implements ISignService {
    @Autowired
    private ISignRepo repo;

    @Override
    protected IGenericRepo<Sign, Integer> getRepo() {
        return repo;
    }

    @Override
    public Page<Sign> listPage(Pageable page) {
        return repo.findAll(page);
    }
}
