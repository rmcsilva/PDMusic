package sample.communication.models;

import sample.models.ServerInformation;

public class MulticastNotificationInformation {

    private int notificationId;
    private String notificationIp;
    private int notificationTcpPort;

    private ServerInformation serverInformation;

    public MulticastNotificationInformation(int notificationId, String notificationIp, int notificationTcpPort) {
        this.notificationId = notificationId;
        this.notificationIp = notificationIp;
        this.notificationTcpPort = notificationTcpPort;
        serverInformation = new ServerInformation(notificationIp, notificationTcpPort);
    }

    public int getNotificationId() {
        return notificationId;
    }

    public String getNotificationIp() {
        return notificationIp;
    }

    public int getNotificationTcpPort() {
        return notificationTcpPort;
    }

    public ServerInformation getServerInformation() {
        return serverInformation;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof MulticastNotificationInformation)) {
            return false;
        }

        MulticastNotificationInformation multicastNotificationInformation = (MulticastNotificationInformation) obj;

        return notificationId == multicastNotificationInformation.getNotificationId() &&
                serverInformation.equals(multicastNotificationInformation.getServerInformation());
    }
}
