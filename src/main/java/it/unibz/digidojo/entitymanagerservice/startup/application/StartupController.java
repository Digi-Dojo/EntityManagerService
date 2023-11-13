package it.unibz.digidojo.entitymanagerservice.startup.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unibz.digidojo.entitymanagerservice.startup.domain.usecases.ManageStartup;
import it.unibz.digidojo.entitymanagerservice.startup.domain.usecases.SearchStartup;
import it.unibz.digidojo.entitymanagerservice.startup.domain.Startup;
import it.unibz.digidojo.sharedmodel.request.StartupRequest;

@RestController
@RequestMapping(path = "/v1/startup")
public class StartupController {
    private final ManageStartup manageStartup;
    private final SearchStartup searchStartups;

    @Autowired
    public StartupController(ManageStartup manageStartup, SearchStartup searchStartups) {
        this.manageStartup = manageStartup;
        this.searchStartups = searchStartups;
    }

    @PostMapping
    public Startup createStartup(@Validated @RequestBody StartupRequest request) {
        return manageStartup.createStartup(request.name(), request.description());
    }

    @GetMapping("/{id}")
    public Startup findById(@PathVariable("id") Long id) {
        return searchStartups.findById(id);
    }

    @GetMapping
    public List<Startup> findAll() {
        return searchStartups.findAll();
    }

    @GetMapping("/name/{name}")
    public Startup findByName(@PathVariable("name") String name) {
        return searchStartups.findByName(name);
    }

    @PatchMapping("/{id}")
    public Startup updateStartup(@PathVariable("id") Long id, @RequestBody StartupRequest request) {
        Startup startup = searchStartups.findById(id), updatedStartup = null;

        if (request.name() != null) {
            updatedStartup = manageStartup.updateStartupName(startup.getName(), request.name());
        }

        if (request.description() != null) {
            updatedStartup = manageStartup.updateStartupDescription(startup.getName(), request.description());
        }

        if (updatedStartup == null) {
            throw new IllegalArgumentException("The request must have at least one field to change");
        }

        return updatedStartup;
    }

    @DeleteMapping("/{id}")
    public void deleteStartup(@PathVariable("id") Long id) {
        Startup startup = searchStartups.findById(id);
        manageStartup.deleteStartup(startup.getName());
    }
}
