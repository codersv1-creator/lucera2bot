/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.utils.Location
 */
package com.lucera2.scripts.altrecbots.model;

import java.util.Optional;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class BotPhrase {
    private final String text;
    private Optional<Integer> sex;
    private Optional<Location> loc;

    public BotPhrase(String string, Optional<Integer> optional, Optional<Location> optional2) {
        this.text = string;
        this.sex = optional;
        this.loc = optional2;
    }

    public BotPhrase(String string, Integer n) {
        this(string, Optional.of(n), Optional.empty());
    }

    public BotPhrase(String string) {
        this(string, Optional.empty(), Optional.empty());
    }

    public String getText() {
        return this.text;
    }

    public Optional<Integer> getSex() {
        return this.sex;
    }

    public BotPhrase setSex(Integer n) {
        this.sex = Optional.ofNullable(n);
        return this;
    }

    public Optional<Location> getLoc() {
        return this.loc;
    }

    public BotPhrase setLoc(Location location) {
        this.loc = Optional.ofNullable(location);
        return this;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        BotPhrase botPhrase = (BotPhrase)object;
        return new EqualsBuilder().append(this.text, botPhrase.text).append(this.sex, botPhrase.sex).append(this.loc, botPhrase.loc).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.text).append(this.sex).append(this.loc).toHashCode();
    }

    public String toString() {
        return "BotPhrase{text='" + this.text + "', sex=" + this.sex + ", loc=" + this.loc + "}";
    }
}

