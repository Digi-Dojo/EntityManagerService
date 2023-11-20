package it.unibz.digidojo.entitymanagerservice.startup.domain.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.unibz.digidojo.entitymanagerservice.startup.domain.model.Startup;
import it.unibz.digidojo.entitymanagerservice.startup.domain.StartupBroadcaster;
import it.unibz.digidojo.entitymanagerservice.startup.domain.StartupRepository;
import it.unibz.digidojo.entitymanagerservice.util.NumberGenerator;

@ExtendWith(MockitoExtension.class)
public class ManageStartupTest {
    @Mock
    private StartupRepository startupRepository;

    @Mock
    private StartupBroadcaster startupBroadcaster;
    private ManageStartup useCase;

    @BeforeEach
    void setUp() {
        useCase = new ManageStartup(startupRepository, startupBroadcaster);
    }

    @Test
    public void itCreatesAStartup() {
        Startup startup = new Startup("GreenGym", "An eco-friendly environment to train in");
        when(startupRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(startupRepository.save(any())).thenReturn(
                new Startup(NumberGenerator.randomPositiveLong(), startup.getName(), startup.getDescription()));

        Startup result = useCase.createStartup(startup.getName(), startup.getDescription());

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

        assertThatThrownBy(() -> useCase.createStartup(startup.getName(), startup.getDescription()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Startup with name " + startup.getName() + " already exists");
    }

    @Test
    public void updateStartupNameUpdatesTheName() {
        String oldName = "GreenGym";
        String newName = "PlanetFitness";
        String description = "Description";
        when(startupRepository.findByName(newName)).thenReturn(Optional.empty());
        when(startupRepository.findByName(oldName)).thenReturn(
                Optional.of(new Startup(NumberGenerator.randomPositiveLong(), oldName, description)));
        when(startupRepository.save(any())).thenReturn(new Startup(NumberGenerator.randomPositiveLong(), newName, description));

        Startup result = useCase.updateStartupName(oldName, newName);
        assertThat(result.getName()).isEqualTo(newName);
    }

    @Test
    public void updateStartupDescriptionUpdatesTheDescription() {
        String name = "GreenGym";
        String description = "Description";
        when(startupRepository.findByName(name)).thenReturn(
                Optional.of(new Startup(NumberGenerator.randomPositiveLong(), name, "old description")));
        when(startupRepository.save(any())).thenReturn(new Startup(NumberGenerator.randomPositiveLong(), name, description));

        Startup result = useCase.updateStartupDescription(name, description);
        assertThat(result.getDescription()).isEqualTo(description);
    }

    @Test
    public void updateStartupNameThrowsForNewNameExisting() {
        String newName = "newName";
        String oldName = "oldName";
        when(startupRepository.findByName(newName)).thenReturn(Optional.of(new Startup(newName, "description")));
        assertThatThrownBy(() -> useCase.updateStartupName(oldName, newName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Startup with name " + newName + " already exists");
    }

    @Test
    public void updateStartupNameThrowsForOldNameNotExisting() {
        String newName = "newName";
        String oldName = "oldName";
        when(startupRepository.findByName(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.updateStartupName(oldName, newName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Startup with name " + oldName + " does not exist");
    }

    @Test
    public void updateStartupDescriptionThrowsForNameNotExisting() {
        String name = "name";
        String description = "description";
        when(startupRepository.findByName(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.updateStartupDescription(name, description))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Startup with name " + name + " does not exist");
    }

    @Test
    public void deleteStartupThrowsForNotExistingStartup() {
        String name = "name";
        when(startupRepository.findByName(anyString()))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.deleteStartup(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Startup with name " + name + " does not exist");
    }
}
