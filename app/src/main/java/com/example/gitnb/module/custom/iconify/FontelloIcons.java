package com.example.gitnb.module.custom.iconify;

import com.joanzapata.iconify.Icon;

/**
 * Created by Rain on 17/2/18.
 */

public enum  FontelloIcons implements Icon {

    fe_location('\uE800'),
    fe_star('\uE823'),
    fe_follower('\uE807'),
    fe_following('\uF184'),
    fe_language('\uE80f'),
    fe_state('\uF281'),
    fe_fork('\uF126'),
    fe_date('\uE811'),
    fe_size('\uF0B2'),
    fe_readme('\uF0F6'),
    fe_contributor('\uF113'),
    fe_circle('\uE84C'),
    fe_event('\uF11E'),
    fe_source('\uF1C9'),
    fe_issue('\uE80E'),
    fe_company('\uE801'),
    fe_release('\uE835'),
    fe_website('\uE815'),
    fe_commit('\uF2A3'),
    fe_request('\uF281'),
    fe_email('\uF0E0'),
    fe_organization('\uF18C'),
    fe_repos('\uF056'),
    fe_folder('\uF115'),
    fe_file('\uF0F6'),
    fe_comment('\uE850'),
    fe_label('\uE802'),
    fe_assignee('\uF1AE'),
    fe_milestone('\uF11E'),
    fe_cancel('\uE839'),
    fe_search('\uE821'),
    fe_more('\uF107'),
    fe_edit('\uE836'),
    fe_clear('\uE82A'),
    fe_delete('\uE829');

    char character;

    FontelloIcons(char character){
        this.character = character;
    }

    @Override
    public String key() {
        return name().replace("_","-");
    }

    @Override
    public char character() {
        return character;
    }
}
