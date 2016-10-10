package com.testography.devintensive.data.storage.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

@Entity(active = true, nameInDb = "REPOSITORIES")
public class Repository {

    @Id
    private Long id;

    @NotNull
    @Unique
    private String remoteId;

    private String repositoryName;

    private String userRemoteId;
}
