/*
 * Copyright (C) 2026 Maria Taylor
 * SPDX-License-Identifier: GPL-3.0-only
 */
package gay.viktoria.mvillagenamer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collection;

public class NameGenerator extends CircularList<ArrayList<String>> {
    //private static final Map<String, List<String>> NAME_MAP = new HashMap<>();
    private static final Random RANDOM = new Random();

    public NameGenerator(Collection<ArrayList<String>> namelist) {
        super();

        if (namelist.isEmpty()) {
            throw new IllegalArgumentException("namelist provided to NameGenerator cannot be empty!");
        }

        this.addAll(new ArrayList<>(namelist));

    }

    public NameGenerator() {
        super();
    }

    public String getName() {
        String name;
        try {
            List<String> nameSet = this.getNext();
            int rndIdx = RANDOM.nextInt(nameSet.size());
            name = nameSet.get(rndIdx);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            name = "";
        }

        return name;
    }
}
