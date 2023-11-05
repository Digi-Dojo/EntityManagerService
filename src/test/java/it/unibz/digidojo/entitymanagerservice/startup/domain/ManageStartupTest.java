package it.unibz.digidojo.entitymanagerservice.startup.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class ManageStartupTest {
    private ManageStartup underTest;

    @Mock
    private StartupRepository startupRepository;

    @Mock
    private StartupBroadcaster startupBroadcaster;

    @BeforeEach
    void setUp() {
        underTest = new ManageStartup(startupRepository, startupBroadcaster);
    }

    @Test
    public void itCreatesAStartup() {
        Startup startup = new Startup("GreenGym", "An eco-friendly environment to train in");

        when(startupRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(startupRepository.save(any())).thenReturn(new Startup(randomPositiveLong(), startup.getName(), startup.getDescription()));

        Startup result = underTest.createStartup(startup.getName(), startup.getDescription());

        assertThat(result).isInstanceOf(Startup.class);
        assertThat(result.getName()).isEqualTo(startup.getName());
        assertThat(result.getDescription()).isEqualTo(startup.getDescription());
        assertThat(result.getId())
                .isNotNull()
                .isGreaterThan(0);
    }

    @Test
    public void createStartupThrowsForExistingStartup() {
        Startup startup = new Startup("GreenGym", "An eco-friendly environment to train in");

        when(startupRepository.findByName(anyString()))
                .thenReturn(Optional.of(new Startup("GreenGym", "An eco-friendly environment to train in")));

        assertThatThrownBy(() -> underTest.createStartup(startup.getName(), startup.getDescription()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Startup with name " + startup.getName() + " already exists");
    }

    @Test
    public void updateStartupNameUpdatesTheName() {
        String oldName = "GreenGym";
        String newName = "PlanetFitness";
        String description = "Description";
        when(startupRepository.findByName(newName)).thenReturn(Optional.empty());
        when(startupRepository.findByName(oldName)).thenReturn(Optional.of(new Startup(randomPositiveLong(), oldName, description)));
        when(startupRepository.save(any())).thenReturn(new Startup(randomPositiveLong(), newName, description));

        Startup result = underTest.updateStartupName(oldName, newName);

        assertThat(result.getName()).isEqualTo(newName);
    }

    @Test
    public void updateStartupDescriptionUpdatesTheDescription() {
        String name = "GreenGym";
        String description = "Description";
        when(startupRepository.findByName(name)).thenReturn(Optional.of(new Startup(randomPositiveLong(), name, "old description")));
        when(startupRepository.save(any())).thenReturn(new Startup(randomPositiveLong(), name, description));

        Startup result = underTest.updateStartupDescription(name, description);

        assertThat(result.getDescription()).isEqualTo(description);
    }

    @Test
    public void updateStartupNameThrowsForNewNameExisting() {
        String newName = "newName";
        String oldName = "oldName";
        when(startupRepository.findByName(newName)).thenReturn(Optional.of(new Startup(newName, "description")));
        assertThatThrownBy(() -> underTest.updateStartupName(oldName, newName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Startup with name " + newName + " already exists");
    }

    @Test
    public void updateStartupNameThrowsForOldNameNotExisting() {
        String newName = "newName";
        String oldName = "oldName";
        when(startupRepository.findByName(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.updateStartupName(oldName, newName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Startup with name " + oldName + " does not exist");
    }

    @Test
    public void updateStartupDescriptionThrowsForNameNotExisting() {
        String name = "name";
        String description = "description";
        when(startupRepository.findByName(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.updateStartupDescription(name, description))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Startup with name " + name + " does not exist");
    }

    @Test
    public void deleteStartupThrowsForNotExistingStartup() {
        String name = "name";
        when(startupRepository.findByName(anyString()))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.deleteStartup(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Startup with name " + name + " does not exist");
    }


    private Long randomPositiveLong() {
        long leftLimit = 1L;
        long rightLimit = 1000L;
        return leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
    }
}
