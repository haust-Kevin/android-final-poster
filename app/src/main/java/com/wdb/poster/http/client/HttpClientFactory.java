package com.wdb.poster.http.client;

import com.wdb.poster.db.api.entity.Connection;
import com.wdb.poster.http.client.impl.HttpClient;

import java.util.HashMap;
import java.util.Map;

public class HttpClientFactory {

    static private Map<Integer, IHttpClient> clients;

    static {
        clients = new HashMap<>();
    }

    public static IHttpClient create(Connection connection) {
        if (connection == null) {
            return new HttpClient();
        } else {
            int connId = connection.getConnId();
            IHttpClient client;

            client = clients.get(connId);
            if (client == null)
                client = new HttpClient();
            ((HttpClient) client).setSingleSession(connection.isSingleSession());
            return client;
        }
    }
}
