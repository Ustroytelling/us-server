package us.usserver.domain.novel.service;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface KomoranService {
    Set<String> tokenizeKeyword(String keyword);
}
