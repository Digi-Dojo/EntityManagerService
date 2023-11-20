package it.unibz.digidojo.entitymanagerservice.startup.domain.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.unibz.digidojo.entitymanagerservice.startup.domain.model.Startup;
import it.unibz.digidojo.entitymanagerservice.startup.domain.StartupRepository;
import it.unibz.digidojo.entitymanagerservice.util.NumberGenerator;

@ExtendWith(MockitoExtension.class)
public class SearchStartupTest {
    @Mock
    private StartupRepository startupRepository;
    private SearchStartup useCase;

    @BeforeEach
    void setUp() {
        useCase = new SearchStartup(startupRepository);
    }

    @Test
    public void findByIdReturnsStartupWithMatchingId() {
        Long id = NumberGenerator.randomPositiveLong();
        String name = "name";
        String description = "description";
        when(startupRepository.findById(anyLong()))
                .thenReturn(Optional.of(new Startup(id, name, description)));

        Startup result = useCase.findById(id);
        assertThat(result)
                .isInstanceOf(Startup.class);
        assertThat(result.getId())
                .isEqualTo(id);
        assertThat(result.getName())
                .isEqualTo(name);
        assertThat(result.getDescription())
                .isEqualTo(description);
    }

    @Test
    public void findByIdThrowsForNotExistingStartup() {
        when(startupRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Long id = NumberGenerator.randomPositiveLong();
        assertThatThrownBy(() -> useCase.findById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Startup with id " + id + " is not present in the database");
    }

    @Test
    public void findAllFindsAllExistingStartups() {
        List<Startup> startups = new ArrayList<>();
        Startup startup1 = new Startup(1L, "name1", "description1");
        Startup startup2 = new Startup(2L, "name2", "description2");
        startups.add(startup1);
        startups.add(startup2);
        when(startupRepository.findAll())
                .thenReturn(new ArrayList<Startup>() {{
                    add(startup1);
                    add(startup2);
                }});
        List<Startup> result = useCase.findAll();
        assertThat(result).isInstanceOf(List.class);
        assertThat(result.size()).isEqualTo(startups.size());
        assertThat(result.get(0)).isInstanceOf(Startup.class);
        assertThat(result.get(0).getId()).isEqualTo(startup1.getId());
    }

    @Test
    public void findAllThrowsForNoExistingStartups() {
        when(startupRepository.findAll())
                .thenReturn(new ArrayList<>());
        assertThatThrownBy(() -> useCase.findAll())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No startups in database");
    }

    @Test
    public void findByNameReturnsStartupWithMatchingName() {
        Long id = NumberGenerator.randomPositiveLong();
        String name = "name";
        String description = "description";
        when(startupRepository.findByName(name))
                .thenReturn(Optional.of(new Startup(id, name, description)));

        Startup result = useCase.findByName(name);
        assertThat(result)
                .isInstanceOf(Startup.class);
        assertThat(result.getId())
                .isEqualTo(id);
        assertThat(result.getName())
                .isEqualTo(name);
        assertThat(result.getDescription())
                .isEqualTo(description);
    }

    @Test
    public void findByNameThrowsForNotExistingStartup() {
        when(startupRepository.findByName(anyString()))
                .thenReturn(Optional.empty());
        String name = "name";
        assertThatThrownBy(() -> useCase.findByName(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Startup with name " + name + " does not exist");
    }
}
