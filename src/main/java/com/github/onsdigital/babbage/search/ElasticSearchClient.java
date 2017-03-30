package com.github.onsdigital.babbage.search;

import com.github.onsdigital.babbage.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.onsdigital.babbage.configuration.Configuration.ELASTIC_SEARCH.getElasticSearchCluster;
import static com.github.onsdigital.babbage.configuration.Configuration.ELASTIC_SEARCH.getElasticSearchServer;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * Created by bren on 16/12/15.
 */
public class ElasticSearchClient {

    private static Client client;
    private static Path searchHome;

    private ElasticSearchClient() {

    }

    public static Client getElasticsearchClient() {
        return client;
    }

    public static void init() throws IOException {
        if (client == null) {
            initTransportClient();
//            initNodeClient();
        }
    }

    protected static void initTransportClient() throws IOException {
        Settings.Builder builder = Settings.builder();

        if (!StringUtils.isBlank(getElasticSearchCluster()))
            builder.put("cluster.name", getElasticSearchCluster());

        Settings settings = builder.build();
        client = TransportClient.builder().settings(settings).build()
                .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(getElasticSearchServer(), Configuration.ELASTIC_SEARCH.getElasticSearchPort())));
    }




}
