package model;

// This is used as a helper class for creating a list of existing game info. Used by the list Games endpoint.
public record GameInfo(Integer gameID, String whiteUsername, String blackUsername, String gameName) {
}
