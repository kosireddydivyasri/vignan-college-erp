import { useEffect, useState } from "react";
import axios from "axios";
import { Card, CardBody, Typography } from "@material-tailwind/react";

export function Home() {
  const [student, setStudent] = useState(null);
  useEffect(() => { const id=localStorage.getItem("studentId"); if(id) axios.get(`http://localhost:8080/api/students/${id}`).then(({data})=>setStudent(data)).catch(()=>{}); }, []);
  return <div className="mt-8 space-y-6"><Card><CardBody><Typography variant="h4">Student Dashboard</Typography><Typography className="mt-1" color="blue-gray">{student ? `Welcome, ${student.studName}.` : "Welcome to Vignan College ERP."}</Typography></CardBody></Card><div className="grid gap-4 md:grid-cols-3"><Card><CardBody><Typography variant="small">Branch</Typography><Typography variant="h6">{student?.major || "-"}</Typography></CardBody></Card><Card><CardBody><Typography variant="small">Year</Typography><Typography variant="h6">{student?.year || "-"}</Typography></CardBody></Card><Card><CardBody><Typography variant="small">Roll Number</Typography><Typography variant="h6">{student?.studRollNo || "-"}</Typography></CardBody></Card></div></div>;
}
export default Home;
