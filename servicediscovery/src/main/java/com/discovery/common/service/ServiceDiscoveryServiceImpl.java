package com.discovery.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ServiceDiscoveryServiceImpl implements ServiceDiscoveryService {

    private ServiceLocationResolver serviceLocationResolver;

    @Override
    public String getServiceEndPoint(String serviceName) {
            return ServiceDiscoveryConstants.HTTP_CONSTANT + serviceLocationResolver.resolve(serviceName);
    }

    @Autowired
    @Qualifier("cloudMapServiceLocationResolver")
    public void setServiceLocationResolver(ServiceLocationResolver serviceLocationResolver) {
        this.serviceLocationResolver = serviceLocationResolver;
    }
}
