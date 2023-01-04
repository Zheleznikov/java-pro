package ru.otus.model;



import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;


/*
fetch = FetchType.EAGER чтобы выполнились тесты в core.repository и crm.service, а так лучше LAZY
 */
@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@NamedEntityGraph(name = "address-and-phone-entity-graph",
        attributeNodes = {@NamedAttributeNode("address"), @NamedAttributeNode("phone")})
public class Client implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Address address;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "client")
    private List<Phone> phone;


    public Client(Long id, String name, Address address, List<Phone> phone) {
        this.id = id;
        this.name = name;
        this.address = address;

        this.phone = phone;
        this.phone.forEach(p -> p.setClient(this));
    }

    @Override
    public Client clone() {
        Client clonedClient = new Client()
                .setId(this.id)
                .setName(this.name)
                .setAddress(new Address(this.getAddress().getId(), this.getAddress().getStreet()));

        var clonedPhone = this.getPhone().stream()
                .map(p -> new Phone(p.getId(), p.getNumber(), clonedClient))
                .toList();

        return clonedClient
                .setPhone(clonedPhone);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address=" + address.getId() +
                ", phone=" + phone +
                '}';
    }
}
