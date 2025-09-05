FROM grafana/grafana:10.4.6

RUN grafana-cli plugins install grafana-piechart-panel

ADD config.ini /etc/grafana/config.ini
ADD provisioning /etc/grafana/provisioning
ADD dashboards /var/lib/grafana/dashboards
