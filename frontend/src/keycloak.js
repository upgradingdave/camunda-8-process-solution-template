import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'https://dave01.gke.c8sm.com/auth',
  realm: 'camunda-platform',
  clientId: 'camunda-react-app',
});

export default keycloak;
