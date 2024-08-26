package com.es.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.es.dto.SearchRequestDto;
import com.es.model.Item;
import com.es.repository.ItemRepository;
import com.es.util.ESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ElasticsearchClient elasticsearchClient;
    private final JSONDataService jsonDataService;

    public Item createIndex(Item item) {
       return itemRepository.save(item);
    }

    public void addItemsFromJson() {
        log.info("Adding items from JSON");
        List<Item> items = jsonDataService.readItemsFromJSON();
        itemRepository.saveAll(items);
    }

    public List<Item> getAllDataFromIndex(String indexName) {
        Query query = ESUtil.createMatchAllQuery();
        log.info("Elasticsearch query {}", query);
        SearchResponse<Item> response = null;
        try {
            response = elasticsearchClient.search(
                    q -> q.index(indexName).query(query), Item.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Elasticsearch response {}", response);
        return extractItemsFromResponse(response);
    }

    public List<Item> extractItemsFromResponse(SearchResponse<Item> searchResponse){
        return searchResponse.hits().hits().stream().map(Hit::source).collect(Collectors.toList());
    }

    public List<Item> searchItemsByFieldAndValue(SearchRequestDto searchRequestDto) {
        Supplier<Query> query = ESUtil.buildQueryForFieldAndValue(searchRequestDto);
        log.info("Elasticsearch query {}", query);
        SearchResponse<Item> response = null;

        try {
            response = elasticsearchClient.search(q -> q.index("items_index").query(query.get()), Item.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Elasticsearch response {}", response);
        return extractItemsFromResponse(response);
    }

    public List<Item> searchItemsByNameAndBrandWithQuery(String name, String brand) {
       return itemRepository.searchByNameAndBrand(name, brand);
    }
}
