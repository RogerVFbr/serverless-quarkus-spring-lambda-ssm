package com.soundlab.mappers;

import com.soundlab.core.GetParameterResponseDTO;

import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

public class GetParameterResponseDTOMapper implements ModelMapper<GetParameterResponse, GetParameterResponseDTO>{
    @Override
    public GetParameterResponseDTO map(GetParameterResponse source) {
        GetParameterResponseDTO target = new GetParameterResponseDTO();
        target.setName(source.parameter().name());
        target.setArn(source.parameter().arn());
        target.setDataType(source.parameter().dataType());
        target.setValue(source.parameter().value());
        target.setLastModifiedDate(source.parameter().lastModifiedDate());
        return target;
    }
}
