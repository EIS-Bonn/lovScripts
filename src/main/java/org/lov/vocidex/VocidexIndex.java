package org.lov.vocidex;

import java.io.Closeable;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * A connection to a specific named index on an ElasticSearch cluster
 * 
 * @author Richard Cyganiak
 */
public class VocidexIndex implements Closeable {
	private final String clusterName;
	private final String hostName;
	private final String indexName;
	private Client client = null;
	
	public VocidexIndex(String clusterName, String hostName, String indexName) {
		this.clusterName = clusterName;
		this.hostName = hostName;
		this.indexName = indexName;
	}

	/**
	 * Connects to the cluster if not yet connected. Is called implicitly by
	 * all operations that require a connection. 
	 */
	public void connect() {
		if (client != null) return;
		Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", clusterName).build();
		client = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(hostName, 9300));
	}
	
	public void close() {
		if (client == null) return;
		client.close();
		client = null;
	}
	
	public boolean exists() {
		connect();
		return client.admin().indices().exists(Requests.indicesExistsRequest(indexName)).actionGet().isExists();
	}
	
	public void delete() {
		connect();
		client.admin().indices().prepareDelete(indexName).execute();		
	}
	
	public boolean create() {
		connect();
		//create index with specific settings
		if (!client.admin().indices().create(Requests.createIndexRequest(indexName).settings(JSONHelper.readFile("mappings/settings.json"))).actionGet().isAcknowledged()) {
			return false;
		}
		if (!setMapping("class", "mappings/class.json")) return false;
		if (!setMapping("property", "mappings/property.json")) return false;
		if (!setMapping("datatype", "mappings/datatype.json")) return false;
		if (!setMapping("instance", "mappings/instance.json")) return false;
		if (!setMapping("vocabulary", "mappings/vocabulary.json")) return false;
		if (!setMapping("person", "mappings/person.json")) return false;
		if (!setMapping("organization", "mappings/organization.json")) return false;
		if (!setMapping("pilot", "mappings/pilot.json")) return false;
		
		return true;
	}
	
	public boolean setMapping(String type, String jsonConfigFile) {
		String json = JSONHelper.readFile(jsonConfigFile);
		if (!client.admin().indices().preparePutMapping().setIndices(indexName).setType(type).setSource(json).execute().actionGet().isAcknowledged()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Adds a document (that is, a JSON structure) to the index.
	 * @return The document's id
	 */
	public String addDocument(VocidexDocument document) {
		return client
				.prepareIndex(indexName, document.getType(), document.getId())
				.setSource(document.getJSONContents())
				.execute().actionGet().getId();
	}
}
