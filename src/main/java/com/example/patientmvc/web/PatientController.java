package com.example.patientmvc.web;

import com.example.patientmvc.entities.Patient;
import com.example.patientmvc.repositories.PatientRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.*;
import java.util.List;

@Controller
@AllArgsConstructor
public class PatientController {
    private PatientRepository patientRepository;
    @GetMapping(path="/user/index")
    public  String patients(Model model,
                            @RequestParam(name="page",defaultValue = "0") int page,
                            @RequestParam(name="size",defaultValue = "5") int size,
                            @RequestParam(name="keyword",defaultValue = "") String keyword){
        Page<Patient> pagepatients=
                patientRepository.findByNomContains(keyword,PageRequest.of(page,size));
        model.addAttribute("listpatients",pagepatients.getContent());
        model.addAttribute("pages",new int[pagepatients.getTotalPages()]);
        model.addAttribute("keyword",keyword);
        return "patients";

    }

    @GetMapping("/admin/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String delete(Long id,String keyword){
        patientRepository.deleteById(id);
        return "redirect:/user/index?keyword="+keyword;

    }
    @GetMapping("/")
    public String home(){

        return "redirect:/user/index";
    }
    @GetMapping("/patients")
    @ResponseBody
    public List<Patient> listPatients(){

        return patientRepository.findAll();
    }
     @GetMapping("/admin/formPatients")
     @PreAuthorize("hasRole('ROLE_ADMIN')")
     public String formPatients(Model model){
        model.addAttribute("patient",new Patient());
        return "formPatients";
    }
    @PostMapping(path="/admin/save")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String save(Model model, @Valid Patient patient, BindingResult bindingResult){
        if(bindingResult.hasErrors()) return "formPatients";
        patientRepository.save(patient);
        return "redirect:/index";
    }
    @GetMapping("/admin/editPatient")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editPatient(Model model,Long id){
        Patient patient=patientRepository.findById(id).orElse(null);
        if(patient==null) throw new RuntimeException("patient introuvable");
        model.addAttribute("patient",patient);
        return "editPatient";
    }


}
