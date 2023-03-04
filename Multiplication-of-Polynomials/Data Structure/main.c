#include <stdio.h>
#include <stdlib.h>
#define MAX_DEGREE 9

typedef struct{
    int degree; // 다항식의 최고차항
    int exp[MAX_DEGREE]; // 지수의 배열
    int coef[MAX_DEGREE]; // 계수의 배열
}polynomial;

polynomial polyA(){ // A(x) 입력받기
    polynomial A;
    printf("A(x)의 최고차항 입력 : ");
    scanf("%d",&A.degree);
    if(A.degree>9||A.degree<0) exit(0);
    printf("계수 오름차순으로 입력\n");
    for(int i=0;i<A.degree+1;i++){
        printf("%d의 계수 : ",i);
        scanf("%d",&A.coef[i]);
        A.exp[i]=i;
    }
    return A;
}

polynomial polyB(){ // B(x) 입력받기
    polynomial B;
    printf("B(x)의 최고차항 입력 : ");
    scanf("%d",&B.degree);
    if(B.degree>9||B.degree<0) exit(0);
    printf("계수 오름차순으로 입력\n");
    for(int i=0;i<B.degree+1;i++){
        printf("%d의 계수 : ",i);
        scanf("%d",&B.coef[i]);
        B.exp[i]=i;
    }
    return B;
}

void showpoly(polynomial p) { // 함수 출력하기
    int idx=0;
    while(idx<=p.degree){
        printf("%dx^%d ",p.coef[idx],idx);
        if(p.coef[idx+1]>=0 && idx+1<=p.degree){
            printf("+");
        }
        idx++;
    }
    printf("\n");
}

polynomial D; // D(x)
void polymult(polynomial p,polynomial q){
    int avail=0; // 인텍스 위치
    for(int i=0;i<p.degree+1;i++){
        if(p.coef[i]!=0){
            for(int j=0;j<q.degree+1;j++){
                if(p.coef[i]*q.coef[j]!=0){
                    if(avail==0){
                        D.exp[avail]=p.exp[i]+q.exp[j];
                        D.coef[avail]=p.coef[i]*q.coef[j];
                        avail++;
                    }
                    else{ //avail>0
                        int num=0;
                        int cnt=0;
                        for(int k=0;k<avail;k++){
                            if(D.exp[k]==i+j){
                                num=0;
                                num=k;
                                cnt++;
                            }
                            else continue;
                        }
                        if(cnt==1){
                            D.coef[num]=D.coef[num]+p.coef[i]*q.coef[j];
                        }
                        else if(cnt==0){
                            if(avail==1){
                                if(D.exp[0]<p.exp[i]+q.exp[j]){
                                    D.exp[avail]=D.exp[0];
                                    D.coef[avail]=D.coef[0];
                                    D.exp[0]=p.exp[i]+q.exp[j];
                                    D.coef[0]=p.coef[i]*q.coef[j];
                                    avail++;
                                }
                                else if(D.exp[0]>p.exp[i]+q.exp[j]){
                                    D.exp[avail]=p.exp[i]+q.exp[j];
                                    D.coef[avail]=p.coef[i]*q.coef[j];
                                    avail++;
                                }
                            }
                            else if(avail>1){
                                if(p.exp[i]+q.exp[j]>D.exp[0]){ // 지수 i+j가 가장 클 때,
                                    for(int b=avail;b>0;b--){
                                        D.exp[b]=D.exp[b-1];
                                        D.coef[b]=D.coef[b-1];
                                    }
                                    D.exp[0]=p.exp[i]+q.exp[j];
                                    D.coef[0]=p.coef[i]*q.coef[j];
                                    avail++;
                                }
                                else if(p.exp[i]+q.exp[j]<D.exp[avail-1]){ // 지수 i+j가 가장 작을 때
                                    D.exp[avail]=p.exp[i]+q.exp[j];
                                    D.coef[avail]=p.coef[i]*q.coef[j];
                                    avail++;
                                }
                                else{
                                    for(int a=0;a<avail-2;a++){
                                        if(D.exp[a]>p.exp[i]+q.exp[j] && p.exp[i]+q.exp[j]>D.exp[a+1]){
                                            
                                            for(int b=avail;b>a+1;b--){
                                                D.exp[b]=D.exp[b-1];
                                                D.coef[b]=D.coef[b-1];
                                            }
                                            D.exp[a+1]=p.exp[i]+q.exp[j];
                                            D.coef[a+1]=p.coef[i]*q.coef[j];
                                            avail++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    for(int i=0;i<avail;i++){ // D(x) 출력하기
        printf("%dx^%d ",D.coef[i],D.exp[i]);
        if(D.coef[i+1]>0){
            printf("+");
        }
    }
    printf("\n");
    printf("avail : %d\n",avail); // avail값 확인하기
}

int main(){
    polynomial a,b;
    for(int i=0;i<3;i++){
        printf("------------%d번째------------\n",i+1);
        a = polyA();
        b = polyB();
        
        printf("A(x) : ");
        showpoly(a);
        printf("B(x) : ");
        showpoly(b);
        printf("D(x) : ");
        polymult(a,b);
    }
    return 0;
}
