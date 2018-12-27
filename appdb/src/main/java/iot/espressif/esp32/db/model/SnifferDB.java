package iot.espressif.esp32.db.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import iot.espressif.esp32.db.dao.DaoSession;
import iot.espressif.esp32.db.dao.SnifferDBDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

/**
 * Entity mapped to table "SNIFFER_DB".
 */
@Entity(active = true)
public class SnifferDB {

    @Id
    private Long id;
    private Integer type;
    private String bssid;
    private Long utc_time;
    private Integer rssi;
    private Integer channel;
    private String device_mac;
    private String organization;
    private String name;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1341640006)
    private transient SnifferDBDao myDao;
    @Generated(hash = 189106508)
    public SnifferDB(Long id, Integer type, String bssid, Long utc_time, Integer rssi,
            Integer channel, String device_mac, String organization, String name) {
        this.id = id;
        this.type = type;
        this.bssid = bssid;
        this.utc_time = utc_time;
        this.rssi = rssi;
        this.channel = channel;
        this.device_mac = device_mac;
        this.organization = organization;
        this.name = name;
    }
    @Generated(hash = 632670346)
    public SnifferDB() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBssid() {
        return this.bssid;
    }
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }
    public Long getUtc_time() {
        return this.utc_time;
    }
    public void setUtc_time(Long utc_time) {
        this.utc_time = utc_time;
    }
    public Integer getRssi() {
        return this.rssi;
    }
    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }
    public Integer getChannel() {
        return this.channel;
    }
    public void setChannel(Integer channel) {
        this.channel = channel;
    }
    public String getDevice_mac() {
        return this.device_mac;
    }
    public void setDevice_mac(String device_mac) {
        this.device_mac = device_mac;
    }
    public String getOrganization() {
        return this.organization;
    }
    public void setOrganization(String organization) {
        this.organization = organization;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 44867276)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSnifferDBDao() : null;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getType() {
        return this.type;
    }
    public void setType(Integer type) {
        this.type = type;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
