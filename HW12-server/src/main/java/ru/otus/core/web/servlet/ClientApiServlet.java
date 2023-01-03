package ru.otus.core.web.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.otus.dao.ClientDao;
import ru.otus.dto.ClientRq;
import ru.otus.dto.CreateClientRs;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientApiServlet extends HttpServlet {

    private static final int ID_PATH_PARAM_POSITION = 1;

    private final ClientDao clientDao;
    private final Gson gson;

    public ClientApiServlet(ClientDao clientDao, Gson gson) {
        this.clientDao = clientDao;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Client> clients = clientDao.findAll();

        response.setContentType("application/json;charset=UTF-8");

        List<ClientRq> rq = new ArrayList<>();
        clients.forEach(c -> rq.add(new ClientRq()
                .setName(c.getName())
                .setId(c.getId())
                .setAddress(c.getAddress().getStreet())
                .setPhones(c.getPhone().stream().map(Phone::getNumber).toList())
        ));

        ServletOutputStream out = response.getOutputStream();
        System.out.println(gson.toJson(rq));
        out.print(gson.toJson(rq));
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String body = request.getReader().lines().reduce("", String::concat);
//        System.out.println(body);
        var rs = gson.fromJson(body, CreateClientRs.class);


        clientDao.saveClient(new Client()
                .setName(rs.getName())
                .setAddress(new Address(null, rs.getStreet()))
                .setPhone(Collections.singletonList(new Phone(null, rs.getPhone()))));


        ServletOutputStream out = response.getOutputStream();
        out.print("{\"status\": \"client saved\"}");
    }


}
