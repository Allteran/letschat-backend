package io.allteran.letschatbackend.util;

import io.allteran.letschatbackend.domain.Interest;
import io.allteran.letschatbackend.service.InterestService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@DependsOn(value = {"interestService"})
public class Initializer {
    private final InterestService interestService;
    @PostConstruct
    public void initDb() {
        initInterests();
    }

    private void initInterests() {
        if(interestService.findAll().isEmpty()) {
            interests().forEach(interestService::create);
        }
    }
    private List<Interest> interests() {
       return Arrays.asList(
               new Interest("Software"),
               new Interest("Video games"),
               new Interest("News"),
               new Interest("Books"),
               new Interest("Music"),
               new Interest("Fashion"),
               new Interest("Relationships"),
               new Interest("Education"),
               new Interest("Finance"),
               new Interest("Movies"),
               new Interest("Children"),
               new Interest("Science"),
               new Interest("Art"),
               new Interest("Food"),
               new Interest("Travel"),
               new Interest("Psychology")
       );
    }
}
