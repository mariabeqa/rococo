package org.rococo.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Countries {

    RUSSIA("Россия" ),
    AUSTRALIA("Австралия");

    @Getter
    @Setter
    String name;

}
