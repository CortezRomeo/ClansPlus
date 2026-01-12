package com.cortezromeo.clansplus.enums;

public enum CustomHeadCategory {
   ALPHABET(1), ANIMALS(2), BLOCKS(3), DECORATION(4), FOOD_DRINKS(5), HELMETS(11), HUMANS(6), HUMANOID(7), MISCELLANEOUS(8), MONSTERS(9), PLANTS(10);

    private int id;

   CustomHeadCategory(int id) {
       this.id = id;
   }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
