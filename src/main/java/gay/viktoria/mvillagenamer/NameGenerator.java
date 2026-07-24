/*
 * Copyright (C) 2026 Maria Taylor
 * SPDX-License-Identifier: GPL-3.0-only
 */
package gay.viktoria.mvillagenamer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Arrays;
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

        this.add(new ArrayList<>(List.of("Avraham", "Abraham")));
        this.add(new ArrayList<>(List.of("Yitzchak", "Isaac")));
        this.add(new ArrayList<>(List.of("Yaakov", "Jacob")));
        this.add(new ArrayList<>(List.of("Moshe", "Moses")));
        this.add(new ArrayList<>(List.of("Aharon", "Aaron")));
        this.add(new ArrayList<>(List.of("Shlomo", "Solomon")));
        this.add(new ArrayList<>(List.of("Eliyahu", "Elijah")));
        this.add(new ArrayList<>(List.of("Yehuda", "Judah")));
        this.add(new ArrayList<>(List.of("Yosef", "Joseph")));
        this.add(new ArrayList<>(List.of("Yonatan", "Jonathan")));
        this.add(new ArrayList<>(List.of("Shoshana", "Shoshanah")));
        this.add(new ArrayList<>(List.of("Tziporah", "Tzipora", "Zipporah")));
        this.add(new ArrayList<>(List.of("Devorah", "Deborah")));
        this.add(new ArrayList<>(List.of("Chana", "Hannah")));
        this.add(new ArrayList<>(List.of("Rivka", "Rebecca")));

        for (String name : Arrays.asList(
                "Sarah", "Leah", "Rachel", "Miriam", "Naomi", "Avigail", "Tamar", "Batya", "Yael",
                "Shimon", "Levi", "Daniel", "Noach", "Zev",
                "Dov-Ber", "Hershel", "Moishe", "Mendel", "Velvel", "Motke",
                "Faige", "Golda", "Zlata", "Ruchel", "Yente", "Baila", "Soreh", "Liebke")) {
            this.add(new ArrayList<>(Collections.singletonList(name)));
        }
    }

    static {
        /*
        // Names with alternate spellings
        NAME_MAP.put("Avraham", Arrays.asList("Avraham", "Abraham"));
        NAME_MAP.put("Yitzchak", Arrays.asList("Yitzchak", "Isaac"));
        NAME_MAP.put("Yaakov", Arrays.asList("Yaakov", "Jacob"));
        NAME_MAP.put("Moshe", Arrays.asList("Moshe", "Moses"));
        NAME_MAP.put("Aharon", Arrays.asList("Aharon", "Aaron"));
        NAME_MAP.put("Shlomo", Arrays.asList("Shlomo", "Solomon"));
        NAME_MAP.put("Eliyahu", Arrays.asList("Eliyahu", "Elijah"));
        NAME_MAP.put("Yehuda", Arrays.asList("Yehuda", "Judah"));
        NAME_MAP.put("Yosef", Arrays.asList("Yosef", "Joseph"));
        NAME_MAP.put("Yonatan", Arrays.asList("Yonatan", "Jonathan"));
        NAME_MAP.put("Shoshana", Arrays.asList("Shoshana", "Shoshanah"));
        NAME_MAP.put("Tziporah", Arrays.asList("Tziporah", "Tzipora", "Zipporah"));
        NAME_MAP.put("Devorah", Arrays.asList("Devorah", "Deborah"));
        NAME_MAP.put("Chana", Arrays.asList("Chana", "Hannah"));
        NAME_MAP.put("Rivka", Arrays.asList("Rivka", "Rebecca"));

        // Names with only one common form
        for (String name : Arrays.asList(
                "Sarah", "Leah", "Rachel", "Miriam", "Naomi", "Avigail", "Tamar", "Batya", "Yael",
                "Shimon", "Levi", "Daniel", "Noach", "Zev",
                "Dov-Ber", "Hershel", "Moishe", "Mendel", "Velvel", "Motke",
                "Faige", "Golda", "Zlata", "Ruchel", "Yente", "Baila", "Soreh", "Liebke"
        )) {
            NAME_MAP.put(name, Collections.singletonList(name));
        }
        */
    }

    public String getName() {
        /*
         * List<String> baseNames = new ArrayList<>(NAME_MAP.keySet());
         * String baseName = baseNames.get(RANDOM.nextInt(baseNames.size()));
         * List<String> variations = NAME_MAP.get(baseName);
         * return variations.get(RANDOM.nextInt(variations.size()));
         */
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
