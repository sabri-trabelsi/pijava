package tn.esprit.interfaces;

import java.util.List;

public interface IService <T> {
    // Interface for CRUD operations
    void ajouter(T t); // Add an entity
    void modifier(T t); // Modify an entity
    void supprimer(T t); // Delete an entity
    List<T> rechercher(); // Search for entities
    List<T> rechercher(String query); // Search for entities with a specific query
    T rechercherById(int id); // Search for an entity by its ID
}
