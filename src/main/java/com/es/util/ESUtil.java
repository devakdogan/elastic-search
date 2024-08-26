package com.es.util;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.es.dto.SearchRequestDto;
import lombok.experimental.UtilityClass;
import java.util.function.Supplier;

@UtilityClass
public class ESUtil {
    public static Query createMatchAllQuery() {
        return Query.of(q -> q.matchAll(new MatchAllQuery.Builder().build()));
    }

    public static Supplier<Query> buildQueryForFieldAndValue(SearchRequestDto searchRequestDto) {
    return () -> Query.of(q -> q.match(buildMatchQueryForFieldAndValue(searchRequestDto)));
    }

    private static MatchQuery buildMatchQueryForFieldAndValue(SearchRequestDto searchRequestDto) {
        return new MatchQuery.Builder()
                .field(searchRequestDto.getFieldName().get(0))
                .query(searchRequestDto.getSearchValue().get(0))
                .build();
    }
}
