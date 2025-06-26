package org.rococo.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Countries {

    RUSSIA(UUID.fromString("11f0525d-71e5-30c5-aec0-0242ac110004"), "Россия" ),
    AUSTRALIA(UUID.fromString("11f0525d-7122-8188-aec0-0242ac110004"), "Австралия");

    @Getter
    @Setter
    UUID id;

    @Getter
    @Setter
    String name;
}
