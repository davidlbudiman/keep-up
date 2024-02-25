package xyz.davidbudiman.controllers.records;

public record Response<D>(D data, Error error) {
}
