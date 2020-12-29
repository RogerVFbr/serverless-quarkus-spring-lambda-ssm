package com.soundlab.services;

import com.soundlab.core.GetParameterResponseDTO;
import com.soundlab.core.PutParameterRequestDTO;
import com.soundlab.core.PutParameterResponseDTO;
import com.soundlab.mappers.GetParameterResponseDTOMapper;
import com.soundlab.mappers.PutParameterResponseDTOMapper;
import com.soundlab.utils.DurationMetric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.DeleteParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.ParameterTier;
import software.amazon.awssdk.services.ssm.model.ParameterType;
import software.amazon.awssdk.services.ssm.model.PutParameterRequest;
import software.amazon.awssdk.services.ssm.model.PutParameterResponse;

@Service
public class SsmService {

    private SsmClient client;

    @Autowired
    public SsmService(SsmClient client) {
        this.client = client;
    }

    public GetParameterResponseDTO getParameterValue(String param) {
        DurationMetric duration = new DurationMetric();
        GetParameterResponse response = executeSdkGetParameter(param);
        duration.measure("AWS SSM parameter retrieved in");
        return new GetParameterResponseDTOMapper().map(response);
    }

    public PutParameterResponseDTO putParameter(PutParameterRequestDTO data) {
        DurationMetric duration = new DurationMetric();
        PutParameterResponse response = executeSdkPutParameter(data);
        duration.measure("AWS SSM parameter put in");
        return new PutParameterResponseDTOMapper().map(response);
    }

    public String deleteParameter(String param) {
        DurationMetric duration = new DurationMetric();
        executeSdkDeleteParameter(param);
        duration.measure("AWS SSM parameter deleted in");
        return String.format("Parameter '%s' successfully deleted.", param);
    }

    private GetParameterResponse executeSdkGetParameter(String param) {
        GetParameterRequest request = GetParameterRequest.builder()
            .name(param)
            .withDecryption(true)
            .build();
        return client.getParameter(request);
    }

    private PutParameterResponse executeSdkPutParameter(PutParameterRequestDTO data) {
        PutParameterRequest request = PutParameterRequest.builder()
            .name(data.getName())
            .description(data.getDescription())
            .tier(ParameterTier.STANDARD)
            .type(ParameterType.SECURE_STRING)
            .dataType("text")
            .value(data.getValue())
            .build();
        return client.putParameter(request);
    }

    private void executeSdkDeleteParameter(String name) {
        DeleteParameterRequest request = DeleteParameterRequest.builder()
            .name(name)
            .build();
        client.deleteParameter(request);
    }
}
