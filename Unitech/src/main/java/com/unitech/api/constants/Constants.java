package com.unitech.api.constants;

public class Constants {

    public enum UserStatuses{
        Active(1),
        Passive(2);

        public final int id;

        private UserStatuses(int id) {
            this.id = id;
        }
    }


}
