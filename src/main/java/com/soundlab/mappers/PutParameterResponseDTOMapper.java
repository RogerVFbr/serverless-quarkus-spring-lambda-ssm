package com.soundlab.mappers;

import com.soundlab.core.PutParameterResponseDTO;

import software.amazon.awssdk.services.ssm.model.PutParameterResponse;

public class PutParameterResponseDTOMapper implements ModelMapper<PutParameterResponse, PutParameterResponseDTO>{
    @Override
    public PutParameterResponseDTO map(PutParameterResponse source) {
        PutParameterResponseDTO target = new PutParameterResponseDTO();
        target.setTier(source.tier().toString());
        target.setVersion(source.version());
        return target;
    }
}
