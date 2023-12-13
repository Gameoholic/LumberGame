package xyz.gameoholic.lumbergame.config;

public record LumberConfig(StringsConfig strings) {
//    This customization is intended to be used for validation and should be kept as simple as possible.
//
//    For example, we can ensure that the name and address provided to our Person record aren’t null using the following constructor implementation:
//
//    public record Person(String name, String address) {
//        public Person {
//            Objects.requireNonNull(name);
//            Objects.requireNonNull(address);
//        }
//    }
}
