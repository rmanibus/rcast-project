package fr.sciam.rcast.ext;

import fr.sciam.rcast.RegisterRcast;

@RegisterRcast(appName = "the-app")
public interface FakeService {
    String method1(String arg1);
    String throwingMethod();
}
