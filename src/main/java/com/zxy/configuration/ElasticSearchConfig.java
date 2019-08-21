package com.zxy.configuration;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by ZhouXinyu on 2019/6/24 15:38.
 */
@Configuration
public class ElasticSearchConfig {
    @Bean
    public TransportClient client() throws UnknownHostException {
        //端口为elasticsearch的TCP端口9300，不是http的9200
        TransportAddress transportAddress = new TransportAddress(InetAddress.getByName("66.42.64.129"), 9300);

        Settings settings = Settings.builder().put("cluster.name", "ashin").build();

        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(transportAddress);

        return client;
    }
}
