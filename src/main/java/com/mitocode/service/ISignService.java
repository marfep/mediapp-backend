package com.mitocode.service;

import com.mitocode.model.Patient;
import com.mitocode.model.Sign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISignService extends ICRUD<Sign,Integer> {

    Page<Sign> listPage(Pageable page);
}
