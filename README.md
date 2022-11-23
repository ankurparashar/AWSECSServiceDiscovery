## AWS ECS Service Discovery

### Project Overview
This project is used as a submodule in the SpringBoot applications. Also, this is non excecuatable project contains AWS ECS ServiceDiscovery SDK Imeplementation.

Scenario: Consider a service “service-a” which has an API which requires some data from “service-b”.

Solution: “service-a” will internally call the api of “service-b” using service discovery mechanism.

Implementation:
- Discover NameSpace
- Get all ECS Instances
- Get all docker containers for specific service
- Randomly pick any service


Most of the Implementation is written in CloudMapServiceLocationResolver.

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

### How to Use this Project
- Use this SpringBoot application as an Internal Project. Either directly copy all the classes in your existing application or Use this SpringBoot project as submodule in your existing application.
- Update the AWS_CLOUD_NAMESPACE in AwsServiceDiscoveryConstants Class

Note:: Make sure to print the logs so that it will help you to understand the communication between infrastructure.


