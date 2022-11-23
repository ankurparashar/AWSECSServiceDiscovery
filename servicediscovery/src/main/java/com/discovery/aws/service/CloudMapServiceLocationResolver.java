package com.discovery.aws.service;

import com.amazonaws.services.servicediscovery.AWSServiceDiscovery;
import com.amazonaws.services.servicediscovery.AWSServiceDiscoveryClientBuilder;
import com.amazonaws.services.servicediscovery.model.DiscoverInstancesRequest;
import com.amazonaws.services.servicediscovery.model.DiscoverInstancesResult;
import com.amazonaws.services.servicediscovery.model.HealthStatus;
import com.amazonaws.services.servicediscovery.model.HttpInstanceSummary;
import com.discovery.aws.util.AwsServiceDiscoveryConstants;
import com.discovery.common.service.ServiceLocationResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class CloudMapServiceLocationResolver implements ServiceLocationResolver {

    private final Logger logger = LogManager.getLogger(this.getClass().getName());
    private static final Random RAND = new Random(System.currentTimeMillis());

    @Override
    public String resolve(String serviceName) {
        final AWSServiceDiscovery awsServiceDiscovery = AWSServiceDiscoveryClientBuilder.defaultClient();
        logger.info("awsServiceDiscovery " + awsServiceDiscovery);
        final DiscoverInstancesRequest discoverInstancesRequest = new DiscoverInstancesRequest();
        discoverInstancesRequest.setNamespaceName(AwsServiceDiscoveryConstants.AWS_CLOUD_NAMESPACE);
        discoverInstancesRequest.setServiceName(serviceName);
        discoverInstancesRequest.setHealthStatus(HealthStatus.HEALTHY.name());
        logger.info("discoverInstancesRequest " + discoverInstancesRequest);

        final DiscoverInstancesResult discoverInstancesResult = awsServiceDiscovery.discoverInstances(discoverInstancesRequest);
        logger.info("discoverInstancesResult" + discoverInstancesResult);
        final List<HttpInstanceSummary> allInstances = discoverInstancesResult.getInstances();
        final List<String> serviceEndpoints = allInstances.stream()
                .map(result -> result.getAttributes().get(AwsServiceDiscoveryConstants.AWS_INSTANCE_IPV_4_ATTRIBUTE) + ":"
                        + result.getAttributes().get(AwsServiceDiscoveryConstants.AWS_INSTANCE_PORT_ATTRIBUTE))
                .collect(Collectors.toList());
        logger.info("Found instances: {}", serviceEndpoints);

        final HttpInstanceSummary result = allInstances.get(RAND.nextInt(allInstances.size()));
        logger.info("result " + result);
        return result.getAttributes()
                .get(AwsServiceDiscoveryConstants.AWS_INSTANCE_IPV_4_ATTRIBUTE) + ":"
                + result.getAttributes().get(AwsServiceDiscoveryConstants.AWS_INSTANCE_PORT_ATTRIBUTE);
    }
}