package com.soundlab.controllers;

import com.soundlab.core.GetParameterResponseDTO;
import com.soundlab.core.PutParameterRequestDTO;
import com.soundlab.core.PutParameterResponseDTO;
import com.soundlab.services.SsmService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ssm")
public class SsmController {

    private SsmService ssmService;

    @Autowired
    public SsmController(SsmService ssmService) {
        this.ssmService = ssmService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public GetParameterResponseDTO getParameter(@RequestParam String name) {
        return ssmService.getParameterValue(name);
    }

    @RequestMapping(method = RequestMethod.POST)
    public PutParameterResponseDTO putParameter(@RequestBody PutParameterRequestDTO data) {
        return ssmService.putParameter(data);
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = { "application/json"} )
    public String deleteParameter(@RequestParam String name) {
        return ssmService.deleteParameter(name);
    }
}
