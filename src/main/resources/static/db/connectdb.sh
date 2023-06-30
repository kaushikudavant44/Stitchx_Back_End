export VAULT_ADDR='https://vault.agro.services'
vault login --method=oidc
vault write -field signed_key ssh/sign/monuser public_key=@$HOME/.ssh/id_rsa.pub > $HOME/.ssh/id_rsa-cert.pub
chmod 0600 $HOME/.ssh/id_rsa-cert.pub
ssh -i $HOME/.ssh/id_rsa monuser@ssh.platforms.engineering
ssh -L 1531:plantect-dev.cu8mf0m0si3i.ap-northeast-1.rds.amazonaws.com:1521 test