package fr.sciam.rcast.ext;

import fr.sciam.rcast.RegisterRcast;

@RegisterRcast(appName = "the-app")
public interface InnexistingService {
    String method1();
}
