package src.app.workers;

import app.beans.Personne;
import app.exceptions.MyDBException;
import app.helpers.SystemLib;
import app.workers.DbWorkerItf;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DbWorker implements DbWorkerItf {

    private Connection dbConnexion;
    private List<Personne> listePersonnes;
    private int index = 0;

    /**
     * Constructeur du worker
     */
    public DbWorker() {
        listePersonnes = new ArrayList<>();
    }

    @Override
    public void connecterBdMySQL(String nomDB) throws MyDBException {
        //final String url_local = "jdbc:mysql://localhost:3306/" + nomDB;
        final String url_remote = "jdbc:mysql://localhost:3306/" + nomDB;
        final String user = "root";
        final String password = "emf123";

        System.out.println("url:" + url_remote);
        try {
            dbConnexion = DriverManager.getConnection(url_remote, user, password);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void connecterBdHSQLDB(String nomDB) throws MyDBException {
        final String url = "jdbc:hsqldb:file:" + nomDB + ";shutdown=true";
        final String user = "SA";
        final String password = "";
        System.out.println("url:" + url);
        try {
            dbConnexion = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void connecterBdAccess(String nomDB) throws MyDBException {
        final String url = "jdbc:ucanaccess://" + nomDB;
        System.out.println("url=" + url);
        try {
            dbConnexion = DriverManager.getConnection(url);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void deconnecter() throws MyDBException {
        try {
            if (dbConnexion != null) {
                dbConnexion.close();
            }
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    public List<Personne> lirePersonnes() throws MyDBException {
        ArrayList<Personne> tmp = new ArrayList<Personne>();
        try {
            Statement stmt = dbConnexion.createStatement();
            ResultSet res = stmt.executeQuery("SELECT Nom,Prenom FROM t_personne;");
            while (res.next()){
                tmp.add(new Personne(res.getString("Nom"),res.getString("Prenom")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tmp;
    }

    @Override
    public Personne precedentPersonne() throws MyDBException {
        if(listePersonnes.size() == 0) {
            listePersonnes = lirePersonnes();
        }
        return listePersonnes.size() != 0 ? listePersonnes.get(index > 0 ? index-- : index) : null;
    }

    @Override
    public Personne suivantPersonne() throws MyDBException {
        return listePersonnes.size() != 0 ? listePersonnes.get(index < listePersonnes.size() ? index++ : index) : null;
    }

}
