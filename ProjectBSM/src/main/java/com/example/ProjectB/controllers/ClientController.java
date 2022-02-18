package com.example.ProjectB.controllers;

import com.example.ProjectB.Entities.Client;
import com.example.ProjectB.Entities.Role;
import com.example.ProjectB.models.ClientRequest;
import com.example.ProjectB.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ClientController {
    @Autowired
    private ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("client", new Client());

        return "signup_form";
    }

    @GetMapping("/register_admin")
    public String showAdminRegistrationForm(Model model) {
        model.addAttribute("client", new Client());

        return "admin_signup_form";
    }

    @GetMapping("/visit_profile_page")
    public String visitProfilePage(Model model) {
        model.addAttribute("client", new Client());

        return "profile";
    }

    @GetMapping("/add_friend")
    public String addFriend(Model model)
    {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
        } else {
        } String username = principal.toString();
        return "profile";
    }

    @PostMapping("/process_register")
    public String processRegistration(ClientRequest clientRequest)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(clientRequest.getPassword());
        clientRequest.setPassword(encodedPassword);

        boolean result = clientService.saveClient(clientRequest);
        if (result)
            return "successRegistration";
        return "failedRegistration";
    }

    @PostMapping("/process_register_admin")
    public String processRegistrationAdmin(ClientRequest clientRequest)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(clientRequest.getPassword());
        clientRequest.setPassword(encodedPassword);

        boolean result = clientService.saveAdmin(clientRequest);
        if (result)
            return "successRegistration";
        return "failedRegistration";
    }

    @GetMapping("/update")
    public String showUpdateUserInfoForm(Model model) {
        model.addAttribute("client", new Client());
        return "update_form";
    }

    @GetMapping("/update_admin")
    public String showUpdateOtherUserInfoForm(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        for(Role role : clientService.findByUsername(username).getRolesSet())
        {
            if(role.getName().equals("USER"))
                return "failedUpdate";
        }
        model.addAttribute("client", new Client());
        return "admin_update_form";
    }

    @PostMapping("/admin_process_update")
    public String processAdminUpdate(Client client)
    {
        boolean result = clientService.updateClientByAdmin(client);
        if(result)
            return "successUpdate";
        else
            return "failedUpdate";
    }

    @GetMapping("/delete_process")
    public String deleteProcess()
    {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        for(Role role : clientService.findByUsername(username).getRolesSet())
        {
            if(role.getName().equals("USER"))
                return "failedDelete";
        }
        boolean result = clientService.deleteClient(username);
        if(result)
            return "successDelete";
        return "failedDelete";
    }


    @PostMapping("/process_update")
    public String processUpdate(Client client)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(client.getPassword());
        client.setPassword(encodedPassword);
            boolean result = clientService.updateClient(client);
            if(result)
                return "successUpdate";
            else
                return "failedUpdate";
    }

    @GetMapping("")
    public String viewHomePage()
    {
        return "index";
    }

    @GetMapping("/clients")
    public String listClients(Model model) {
        List<Client> listClients = (List<Client>) clientService.findAll();
        model.addAttribute("listClients", listClients);
        return "clients";

    }

//    @GetMapping("/view_finances")
//    public String viewFinances(Model model)
//    {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username;
//        if (principal instanceof UserDetails) {
//            username = ((UserDetails)principal).getUsername();
//        } else {
//            username = principal.toString();
//        }
//        Client client = clientService.getByUsername(username);
//        model.addAttribute("client",client);
//        String balance = client.getAccountBalance();
//        if(balance==null)
//            return "no_bank_account";
//        return "show_bank_account";
//    }
//
//    @GetMapping("/add_finances")
//    public String addFinances(Model model) {
//        model.addAttribute("client", new Client());
//        return "account_balance_form";
//    }
//
//    @PostMapping("/add_finances_process")
//    public String addFinancesProcess(Client client)
//    {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username;
//        if (principal instanceof UserDetails) {
//            username = ((UserDetails)principal).getUsername();
//        } else {
//            username = principal.toString();
//        }
//        Client client2 = clientService.getByUsername(username);
//        try{
//            client2.setAccountBalance(Integer.toString(Integer.parseInt(client2.getAccountBalance())+Integer.parseInt(client.getAccountBalance())));
//        }
//        catch (Exception e)
//        {
//            int save = Integer.parseInt(client.getAccountBalance());
//            String save2 = Integer.toString(save);
//            client2.setAccountBalance(save2);
//        }
//        clientService.updateClientBalance(client2);
//        return "show_bank_account";
//    }



}
