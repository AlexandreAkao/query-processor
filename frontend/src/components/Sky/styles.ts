import styled from 'styled-components';

const multipleBoxShadow = (n: number) => {
  
  let value = `${Math.random() * 2000}px ${Math.random() * 2000}px #FFF`;

  for (let i = 2; i < n; i++) {
    value = `${value} , ${Math.random() * 2000}px ${Math.random() * 2000}px #FFF`
  }

  return value;
} 

export const Stars1 = styled.div`
  width: 1px;
  height: 1px;
  background: transparent;
  box-shadow: ${multipleBoxShadow(700)};
  animation: animStar 50s linear infinite;

  &::after {
    content: " ";
    position: absolute;
    width: 1px;
    height: 1px;
    background: transparent;
    box-shadow: ${multipleBoxShadow(700)};
  }
`

export const Stars2 = styled.div`
  width: 2px;
  height: 2px;
  background: transparent;
  box-shadow: ${multipleBoxShadow(200)};
  animation: animStar 100s linear infinite;

  &::after {
    content: " ";
    position: absolute;
    width: 2px;
    height: 2px;
    background: transparent;
    box-shadow: ${multipleBoxShadow(200)};
  }
` 

export const Stars3 = styled.div`
  width: 3px;
  height: 3px;
  background: transparent;
  box-shadow: ${multipleBoxShadow(100)};
  animation: animStar 150s linear infinite;

  &::after {
    content: " ";
    position: absolute;
    width: 3px;
    height: 3px;
    background: transparent;
    box-shadow: ${multipleBoxShadow(100)};
  }
`