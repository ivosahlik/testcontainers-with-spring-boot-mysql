package cz.ivosahlik.testcontainersystem.constants;


public final class DockerImageConstants {


    // MySQL Docker image
    public static final String MYSQL_IMAGE = "mysql:8.0";

    // Private constructor to prevent instantiation
    private DockerImageConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }
}
