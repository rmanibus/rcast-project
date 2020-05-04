package fr.sciam.rcast.ext;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FakeServiceImpl implements FakeService {
    @Override
    public String method1(String arg1) {
        return arg1;
    }

    public String throwingMethod(){
        throw new FakeException();
    }
}
