package com.votreentreprise.pdlepuration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
public class PdlPageResponse {

    @JsonProperty("_embedded")
    private EmbeddedContent embedded;
    
    @JsonProperty("page")
    private PageMetadata page;
    
    @Data
    public static class EmbeddedContent {
        @JsonProperty("pdlList")
        private List<PdlModel> content;
    }
    
    @Data
    public static class PageMetadata {
        private int size;
        private int totalElements;
        private int totalPages;
        private int number;
    }
    
    public List<PdlModel> getContent() {
        return embedded != null ? embedded.getContent() : List.of();
    }
    
    public Pageable nextPageable() {
        if (page == null || page.getNumber() >= page.getTotalPages() - 1) {
            return null;
        }
        return PageRequest.of(page.getNumber() + 1, page.getSize());
    }
    
    public boolean hasNext() {
        return page != null && page.getNumber() < page.getTotalPages() - 1;
    }
}
